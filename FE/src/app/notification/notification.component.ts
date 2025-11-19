import {
  Component,
  computed,
  DestroyRef,
  inject,
  OnInit,
  signal,
} from "@angular/core";
import { TableComponent } from "~/shared/components/table/table.component";
import { Filter } from "~/shared/enums/filter";
import { Card } from "primeng/card";
import { Pageable } from "~/api/models/pageable";
import { FormBuilder, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { debounceTime } from "rxjs";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { TableLazyLoadEvent } from "primeng/table";
import { invoiceNotificationApiService } from "~/api/services/invoice-notification-api.service";
import { NotificationResponse } from "~/api/models/notification-response";
import { Router } from "@angular/router";
import { Button } from "primeng/button";
import { MessageService } from "primeng/api";
import { DialogComponent } from "~/shared/components/dialog/dialog.component";
import { InputText } from "primeng/inputtext";
import { invoiceInvoiceTrackingApiService } from "~/api/services/invoice-invoice-tracking-api.service";
import { InvoiceResponse } from "~/api/models/invoice-response";
import { Skeleton } from "primeng/skeleton";

@Component({
  selector: "app-notification",
  imports: [
    TableComponent,
    Card,
    Button,
    DialogComponent,
    FormsModule,
    InputText,
    ReactiveFormsModule,
    Skeleton,
  ],
  templateUrl: "./notification.component.html",
  providers: [MessageService],
})
export class NotificationComponent implements OnInit {
  invoiceNotification = inject(invoiceNotificationApiService);
  fb = inject(FormBuilder);
  destroyRef = inject(DestroyRef);
  router = inject(Router);
  messageService = inject(MessageService);
  invoiceInvoiceTrackingApiService = inject(invoiceInvoiceTrackingApiService);
  loadingInvoice = signal<boolean>(false);
  selectedInvoice = signal<InvoiceResponse[]>([]);
  cols = signal([
    {
      field: "invoiceNumber",
      header: "Invoice Number",
      type: Filter.Input,
      sortField: "invoice.invoiceNumber",
    },
    {
      field: "accountName",
      header: "Account User Name",
      type: Filter.Input,
      sortField: "invoice.billingAccount.name",
    },
    {
      field: "message",
      header: "Message",
      type: Filter.Input,
      sortField: "message",
    },
    {
      field: "createdAt",
      header: "Created date",
      type: Filter.Date,
      sortField: "createdAt",
    },
    {
      field: "type",
      header: "Type",
      type: Filter.Dropdown,
      sortField: "type",
      items: [
        { name: "Important", value: "IMPORTANT" },
        { name: "General", value: "GENERAL" },
      ],
    },
    {
      field: "status",
      header: "Status",
      type: Filter.Chip,
      sortField: "status",
      items: [
        { name: "Read", value: "READ" },
        { name: "Unread", value: "UNREAD" },
      ],
    },
  ]);
  items = signal<NotificationResponse[]>([]);
  loading = signal<boolean>(false);
  totalRecords = signal<number>(0);

  pageable = signal<Pageable>({
    page: 0,
    size: 10,
    sort: [],
  });

  tableEvent = signal<TableLazyLoadEvent>({
    first: 0,
    rows: 10,
    sortField: undefined,
    sortOrder: undefined,
  });

  dialog = computed(() => ({
    heading: "View Invoice",
    primary: "",
    secondary: "Close",
  }));

  searchForm = this.fb.group({
    invoiceNumber: this.fb.control<string | undefined>(undefined),
    accountName: this.fb.control<string | undefined>(undefined),
    message: this.fb.control<string | undefined>(undefined),
    createdAt: this.fb.control<string | undefined>(undefined),
    type: this.fb.control<string | undefined>(undefined),
    status: this.fb.control<string>("UNREAD"),
  });
  selected = signal<NotificationResponse[]>([]);
  openInvoiceDialog = signal(false);

  createInvoiceForm = this.fb.group({
    invoiceNumber: this.fb.control<string | undefined>(undefined),
    invoiceDate: this.fb.control<string | undefined>(undefined),
    amount: this.fb.control<number | undefined>(undefined),
    vatAmount: this.fb.control<number | undefined | null>(undefined),
    status: this.fb.control<string>("PENDING"),
  });

  ngOnInit(): void {
    this.searchForm.valueChanges
      .pipe(debounceTime(300), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.pageable.update((currentValue) => ({
          ...currentValue,
          page: 0,
        }));
        this.searchNotifications(this.tableEvent());
      });
  }

  searchNotifications($event: TableLazyLoadEvent) {
    const order = $event.sortOrder && $event.sortOrder > 0 ? "asc" : "desc";
    const sort: string[] = [];
    if ($event.sortField && typeof $event.sortField === "string") {
      sort.push($event.sortField);
      sort.push(order);
    }
    this.pageable.update((currentValue) => ({
      ...currentValue,
      page: ($event.first || 0) / ($event?.rows || 1),
      size: $event.rows || 10,
      sort: sort,
    }));

    const formValues = Object.fromEntries(
      Object.entries(this.searchForm.value).map(([k, v]) => [
        k,
        v ?? undefined,
      ]),
    );
    this.invoiceNotification
      .getAllNotifications({
        pageable: this.pageable(),
        body: formValues,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => {
          this.totalRecords.set(result.totalElements || 0);
          if (result.content) {
            this.items.set(
              result.content.map((item) => ({
                ...item,
                severity: item.status == "READ" ? "success" : "info",
              })),
            );
            this.loading.update((currentValue) => false);
          }
        },
        error: (error) => {
          this.loading.update((currentValue) => false);
        },
        complete: () => {
          this.loading.update((currentValue) => false);
        },
      });
  }

  markAsRead() {
    this.loading.update((currentValue) => true);
    const selectedItems = this.items().filter((x) =>
      this.selected().includes(x),
    );

    const ids: string[] = (selectedItems.map((item) => item.notificationId) ||
      []) as string[];
    if (ids?.length > 0) {
      this.invoiceNotification
        .markNotificationsAsRead({ body: ids })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (result) => {
            ids.forEach((id) =>
              this.items.update((currentValue) =>
                currentValue.filter((item) => item.notificationId !== id),
              ),
            );
            this.messageService.add({
              severity: "success",
              summary: "Success",
              detail: "Marked as read",
            });
          },
          error: (error) => {
            this.loading.update((currentValue) => false);
            this.messageService.add({
              severity: "error",
              summary: "Error",
              detail: "Cannot mark as read",
            });
          },
          complete: () => {
            this.loading.update((currentValue) => false);
          },
        });
    }
  }
  getInvoiceDetails($event: NotificationResponse) {
    if ($event?.invoiceId) {
      this.loadingInvoice.set(true);
      let item: InvoiceResponse | undefined = this.selectedInvoice().find(
        (value: InvoiceResponse) => $event.invoiceId == value.invoiceId,
      );
      if (!item) {
        this.invoiceInvoiceTrackingApiService
          .getInvoice({ invoiceId: $event?.invoiceId })
          .pipe(takeUntilDestroyed(this.destroyRef))
          .subscribe({
            next: (result) => {
              this.selectedInvoice.update((currentValue) => [
                ...currentValue,
                result,
              ]);
              this.createInvoiceForm.patchValue(result);
              this.loadingInvoice.set(false);
            },
            error: (error) => {
              this.loadingInvoice.set(false);
            },
          });
      } else {
        this.createInvoiceForm.patchValue(item);
      }
      this.createInvoiceForm.disable();
      this.openInvoiceDialog.set(true);
    }
  }
}
