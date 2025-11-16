import {Component, computed, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {TableComponent} from '~/shared/components/table/table.component';
import {Filter} from '~/shared/enums/filter';
import {Card} from 'primeng/card';
import {invoiceInvoiceTrackingApiService} from '~/api/services/invoice-invoice-tracking-api.service';
import {Pageable} from '~/api/models/pageable';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {InvoiceSearchRequest} from '~/api/models/invoice-search-request';
import {debounceTime} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {TableLazyLoadEvent, TableRowSelectEvent} from 'primeng/table';
import {Button} from 'primeng/button';
import {InvoiceCreateRequest} from '~/api/models/invoice-create-request';
import {MessageService} from 'primeng/api';
import {DialogComponent} from '~/shared/components/dialog/dialog.component';
import {InputText} from 'primeng/inputtext';
import {DatePicker} from 'primeng/datepicker';
import {InputNumber} from 'primeng/inputnumber';
import {Select} from 'primeng/select';
import {DropDownOptions} from '~/shared/models/dropdown';
import {DialogProperties} from '~/shared/models/dialog';
import {updateInvoices} from '~/api/fn/invoice-tracking-api/update-invoices';
import {ActivatedRoute} from '@angular/router';
import {InvoiceResponse} from '~/api/models/invoice-response';

@Component({
  selector: 'app-dashboard',
  imports: [
    TableComponent,
    Card,
    Button,
    DialogComponent,
    ReactiveFormsModule,
    InputText,
    DatePicker,
    InputNumber,
    Select
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
  providers: [MessageService]
})
export class DashboardComponent implements OnInit {
  invoiceInvoiceTrackingApiService = inject(invoiceInvoiceTrackingApiService);
  fb = inject(FormBuilder);
  destroyRef = inject(DestroyRef);
  messageService = inject(MessageService);
  activateRoute = inject(ActivatedRoute);

  cols = signal([
    {
      field: 'billingAccountName',
      header: 'Account Holder name',
      type: Filter.Input,
      sortField: 'billingAccount.name'
    },
    {
      field: 'invoiceDate',
      header: 'Payment due date',
      type: Filter.Input,
      sortField: 'invoiceDate'
    },
    {
      field: 'status',
      header: 'Payment status',
      type: Filter.Chip,
      sortField: 'status'
    },
    {
      field: 'amount',
      header: 'Payment amount',
      type: Filter.Input,
      sortField: 'amount'
    }
  ]);

  items = signal<InvoiceResponse[]>([])
  loading = signal<boolean>(false);
  createInvoiceLoading = signal<boolean>(false);
  pageable = signal<Pageable>({
    page: 0,
    size: 10,
    sort: []
  })

  tableEvent =signal<TableLazyLoadEvent>({
    first: 0,
    rows: 10,
    sortField: undefined,
    sortOrder: undefined
  })

  openInvoiceDialog = signal(false);
  createInvoiceOption = signal(false);

  dialog = computed(() => ({
    heading: this.createInvoiceOption() ? 'Create new invoice': 'Update invoice',
    primary: this.createInvoiceOption() ? 'Create invoice' : 'Update invoice',
    secondary: 'Cancel'
  }
  ));

  searchForm = this.fb.group({
    billingAccountId: this.fb.control<string | undefined>(undefined),
    invoiceId: this.fb.control<string | undefined>(undefined),
    billingAccountName: this.fb.control<string | undefined>(undefined),
    invoiceNumber: this.fb.control<string | undefined>(undefined),
    invoiceDate: this.fb.control<string | undefined>(undefined),
    status: this.fb.control<string | undefined>(undefined),
    amount: this.fb.control<number | undefined>(undefined)
  });

  createInvoiceForm = this.fb.group({
    invoiceNumber: this.fb.control<string | undefined>(undefined),
    invoiceDate: this.fb.control<string | undefined>(undefined),
    amount: this.fb.control<number | undefined>(undefined),
    vatAmount: this.fb.control<number | undefined | null>(undefined),
    status: this.fb.control<string>('PENDING')
  })

  statusOptions = signal<DropDownOptions[]>([
    {
      label: 'Pending',
      value: 'PENDING'
    },
    {
      label: 'Paid',
      value: 'PAID'
    },
    {
      label: 'Unpaid',
      value: 'UNPAID'
    },
    {
      label: 'Due',
      value: 'DUE'
    },
    {
      label: 'Over due',
      value: 'OVER_DUE'
    },
    {
      label: 'Cancelled',
      value: 'CANCELLED'
    }
  ]);

  totalRecords = signal<number>(0);

  ngOnInit(): void {
    this.searchForm.valueChanges.pipe(debounceTime(300), takeUntilDestroyed(this.destroyRef)).subscribe(() => {
      this.pageable.update(currentValue => ({
        ...currentValue,
        page: 0
      }))
      this.searchInvoices(this.tableEvent());
    });
  }

  searchInvoices($event: TableLazyLoadEvent) {
    if(!this.searchForm.value.billingAccountId) {
      this.activateRoute.paramMap.subscribe((params) => {
        this.searchForm.patchValue({
          billingAccountId: params.get('id') ?? undefined
        });
      });
    }
    console.log('$event', $event)

    const order = $event.sortOrder && $event.sortOrder > 0 ? 'asc' : 'desc';
    const sort: string[] = []
    if ($event.sortField && typeof $event.sortField === 'string') {
      sort.push($event.sortField)
      sort.push(order)
    }
    console.log('here', this.pageable())

    this.pageable.update(currentValue => ({
      ...currentValue,
      page: ($event.first || 0) / ($event?.rows || 1),
      size: $event.rows || 10,
      sort: []
    }))
    const formValues = Object.fromEntries(Object.entries(this.searchForm.value).map(([k, v]) => [k, v ?? undefined]));
    this.invoiceInvoiceTrackingApiService.searchInvoices({
      pageable: this.pageable(),
      body: formValues
    }).pipe(takeUntilDestroyed(this.destroyRef)).subscribe({next: (result) => {
      this.totalRecords.set(result.totalElements || 0)
      if(result.content) {
        this.items.set(result.content.map(item => (
          {...item,
            invoiceDate: new Date(item.invoiceDate || '').toISOString().split('T')[0],
            severity: item.status == 'PAID' ? 'success' : item.status == 'UNPAID' ? 'warn' : 'danger'
          })))
        this.loading.update(currentValue => false)
        console.log('here', result.content)

      }
    }, error: (error) => {
      this.loading.update(currentValue => false)
    }
    })
  }

  createInvoice() {
    this.createInvoiceLoading.set(true);
    this.invoiceInvoiceTrackingApiService.createInvoices({body: this.createInvoiceForm.getRawValue() as InvoiceCreateRequest})
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => {
          this.items.update(currentValue => [result, ...currentValue])
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'Invoice created successfully'})
        }, error: (error) => {
          console.error(error)
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Error creating invoice'})
          this.createInvoiceLoading.set(true);
        }, complete: () => {
          this.createInvoiceLoading.set(false);
        }
      })
    }

  editInvoice($event: InvoiceResponse) {
    console.log('$event',  $event.invoiceDate)

    this.createInvoiceForm.patchValue({
      invoiceNumber: $event.invoiceNumber,
      invoiceDate: new Date($event.invoiceDate || '').toISOString().split('T')[0],
      amount: $event.amount,
      vatAmount: $event.vatAmount,
      status: $event.status
    })
    this.createInvoiceOption.set(false)
    this.openInvoiceDialog.set(true)
  }

  updateInvoice() {
    this.createInvoiceLoading.set(true);
    this.invoiceInvoiceTrackingApiService.updateInvoices({body: this.createInvoiceForm.getRawValue() as InvoiceCreateRequest})
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => {
          this.items.update(currentValue => [result, ...currentValue])
          this.messageService.add({severity: 'success', summary: 'Success', detail: 'Invoice updated successfully'})
        }, error: (error) => {
          console.error(error)
          this.messageService.add({severity: 'error', summary: 'Error', detail: 'Error creating invoice'})
          this.createInvoiceLoading.set(true);
        }, complete: () => {
          this.createInvoiceLoading.set(false);
        }
      })
  }
}
