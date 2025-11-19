import { Component, DestroyRef, inject, OnInit, signal } from "@angular/core";
import { TableComponent } from "~/shared/components/table/table.component";
import { Filter } from "~/shared/enums/filter";
import { Card } from "primeng/card";
import { Pageable } from "~/api/models/pageable";
import { FormBuilder } from "@angular/forms";
import { debounceTime } from "rxjs";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { TableLazyLoadEvent } from "primeng/table";
import { invoiceBillingAccountApiService } from "~/api/services/invoice-billing-account-api.service";
import { BillingAccountResponse } from "~/api/models/billing-account-response";
import { Router } from "@angular/router";
import { Button } from "primeng/button";

@Component({
  selector: "app-account-holder",
  imports: [TableComponent, Card, Button],
  templateUrl: "./account-holder.component.html",
})
export class AccountHolderComponent implements OnInit {
  invoiceBilling = inject(invoiceBillingAccountApiService);
  fb = inject(FormBuilder);
  destroyRef = inject(DestroyRef);
  router = inject(Router);

  cols = signal([
    {
      field: "name",
      header: "Account Holder name",
      type: Filter.Input,
      sortField: "name",
    },
    {
      field: "email",
      header: "Account Holder email",
      type: Filter.Input,
      sortField: "email",
    },
    {
      field: "address",
      header: "Account Holder address",
      type: Filter.Input,
      sortField: "address",
    },
    {
      field: "phone",
      header: "Account Holder phone number",
      type: Filter.Input,
      sortField: "phone",
    },
  ]);

  items = signal<BillingAccountResponse[]>([]);
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

  searchForm = this.fb.group({
    accountId: this.fb.control<string | undefined>(undefined),
    name: this.fb.control<string | undefined>(undefined),
    phone: this.fb.control<string | undefined>(undefined),
    email: this.fb.control<string | undefined>(undefined),
    address: this.fb.control<string | undefined>(undefined),
  });
  openAccountHolderDialog = signal(false);
  createAccountOption = signal(false);

  ngOnInit(): void {
    this.searchForm.valueChanges
      .pipe(debounceTime(300), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.pageable.update((currentValue) => ({
          ...currentValue,
          page: 0,
        }));
        this.searchAccountHolders(this.tableEvent());
      });
  }

  searchAccountHolders($event: TableLazyLoadEvent) {
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
      sort: [],
    }));
    const formValues = Object.fromEntries(
      Object.entries(this.searchForm.value).map(([k, v]) => [
        k,
        v ?? undefined,
      ]),
    );
    this.invoiceBilling
      .searchAccount({
        pageable: this.pageable(),
        body: formValues,
      })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => {
          this.totalRecords.set(result.totalElements || 0);

          if (result.content) {
            this.items.set(result.content);
            this.loading.update((currentValue) => false);
          }
        },
        error: (error) => {
          this.loading.update((currentValue) => false);
        },
      });
  }

  navigateToInvoice($event: BillingAccountResponse) {
    this.router.navigate([`${$event.billingAccId}`, "invoices"]);
  }
}
