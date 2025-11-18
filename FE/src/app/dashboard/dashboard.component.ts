import {Component, computed, DestroyRef, inject, OnInit, signal} from '@angular/core';
import {TableComponent} from '~/shared/components/table/table.component';
import {Filter} from '~/shared/enums/filter';
import {Card} from 'primeng/card';
import {invoiceInvoiceTrackingApiService} from '~/api/services/invoice-invoice-tracking-api.service';
import {Pageable} from '~/api/models/pageable';
import {FormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {debounceTime} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {TableLazyLoadEvent} from 'primeng/table';
import {Button} from 'primeng/button';
import {InvoiceCreateRequest} from '~/api/models/invoice-create-request';
import {MessageService} from 'primeng/api';
import {DialogComponent} from '~/shared/components/dialog/dialog.component';
import {InputText} from 'primeng/inputtext';
import {DatePicker} from 'primeng/datepicker';
import {InputNumber} from 'primeng/inputnumber';
import {Select} from 'primeng/select';
import {DropDownOptions} from '~/shared/models/dropdown';
import {ActivatedRoute} from '@angular/router';
import {InvoiceResponse} from '~/api/models/invoice-response';
import {DatePipe} from '@angular/common';

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
  providers: [MessageService, DatePipe]
})
export class DashboardComponent implements OnInit {
  invoiceInvoiceTrackingApiService = inject(invoiceInvoiceTrackingApiService);
  fb = inject(FormBuilder);
  destroyRef = inject(DestroyRef);
  messageService = inject(MessageService);
  activateRoute = inject(ActivatedRoute);
  datePipe = inject(DatePipe);

  cols = signal([
    {
      field: 'invoiceNumber',
      header: 'Invoice Number',
      type: Filter.Input,
      sortField: 'invoiceNumber'
    },
    {
      field: 'invoiceDate',
      header: 'Payment due date',
      type: Filter.Date,
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
  billingAccountId = signal('')

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
    invoiceNumber: this.fb.control<string>('', [Validators.required]),
    invoiceDate: this.fb.control<string>('', [Validators.required]),
    amount: this.fb.control<number>(0, [Validators.required]),
    vatAmount: this.fb.control<number>(0, [Validators.required]),
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
    this.activateRoute.paramMap.subscribe((params) => {
      this.searchForm.patchValue({
        billingAccountId: params.get('id') ?? undefined
      });
      this.billingAccountId.set(params.get('id') ?? '');
    });
  }

  searchInvoices($event: TableLazyLoadEvent) {
    const order = $event.sortOrder && $event.sortOrder > 0 ? 'asc' : 'desc';
    const sort: string[] = []
    if ($event.sortField && typeof $event.sortField === 'string') {
      sort.push($event.sortField)
      sort.push(order)
    }
    this.pageable.update(currentValue => ({
      ...currentValue,
      page: ($event.first || 0) / ($event?.rows || 1),
      size: $event.rows || 10,
      sort: sort
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
            severity: item.status == 'PAID' ? 'success' : item.status == 'UNPAID' ? 'warn' : 'danger'
          })))
        this.loading.update(currentValue => false)
      }
    }, error: (error) => {
      this.loading.update(currentValue => false)
    }
    })
  }

  createInvoice() {
    this.createInvoiceLoading.set(true);
    const formatted = this.datePipe.transform(this.createInvoiceForm.getRawValue().invoiceDate, "yyyy-MM-dd'T'HH:mm:ss");
    const invoiceItem = {...this.createInvoiceForm.getRawValue(),billingAccountId: this.billingAccountId(), invoiceDate: formatted};

    this.invoiceInvoiceTrackingApiService.createInvoices({body: invoiceItem as InvoiceCreateRequest})
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
          this.openInvoiceDialog.set(false);
        }
      })
    }

  editInvoice($event: InvoiceResponse) {
    this.createInvoiceForm.patchValue({
      invoiceNumber: $event.invoiceNumber,
      invoiceDate: new Date($event.invoiceDate || '').toISOString().split('T')[0],
      amount: $event.amount,
      vatAmount: $event.vatAmount,
      status: $event.status
    })
    this.createInvoiceForm.get('invoiceNumber')?.disable()
    this.createInvoiceOption.set(false)
    this.openInvoiceDialog.set(true)
  }

  updateInvoice() {
    this.createInvoiceLoading.set(true);
    const formatted = this.datePipe.transform(this.createInvoiceForm.getRawValue().invoiceDate, "yyyy-MM-dd'T'HH:mm:ss");
    const invoiceItem = {...this.createInvoiceForm.getRawValue(),billingAccountId: this.billingAccountId(), invoiceDate: formatted};
    //tinqa5
    this.invoiceInvoiceTrackingApiService.updateInvoices({body: invoiceItem as InvoiceCreateRequest})
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
          this.openInvoiceDialog.set(false);
        }
      })
  }

  openCreateNewDialog() {
    this.createInvoiceForm.get('invoiceNumber')?.enable()
    this.createInvoiceForm.reset();
    this.openInvoiceDialog.set(true);
    this.createInvoiceOption.set(true);
  }
}
