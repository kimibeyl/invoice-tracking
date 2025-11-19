import { inject, Injectable, signal } from "@angular/core";
import { BillingAccountResponse } from "~/api/models/billing-account-response";
import { NotificationResponse } from "~/api/models/notification-response";

@Injectable({
  providedIn: "root",
})
export class BillingService {
  // Signal to store card items
  private _billingAccount = signal<BillingAccountResponse[]>([]);
  private _notifications = signal<NotificationResponse[]>([]);

  // Read-only signal for external components
  readonly billingAccount = this._billingAccount.asReadonly();
  readonly notifications = this._notifications.asReadonly();

  // Loading state signal
  private _loading = signal<boolean>(false);
  readonly loading = this._loading.asReadonly();

  setBillingAccountHolders(billing: BillingAccountResponse[]): void {
    this._billingAccount.set(billing);
  }

  setNotifications(notification: NotificationResponse[]): void {
    this._notifications.set(notification);
  }

  setLoading(loading: boolean): void {
    this._loading.set(loading);
  }

  // getBillingAccountHolders(): BillingAccountResponse[] {
  //   return this._billingAccount();
  //   const rawTenant = localStorage.getItem('alp_auth_session');
  //   if (rawTenant) {
  //     try {
  //       let tenant = JSON.parse(rawTenant);
  //       this.authenticationFacilitiesApiService.getFacility({ facilityId: tenant?.selectedTenant?.facilityId ?? '' }).subscribe({
  //         next: (response: FacilityResponse) => {
  //           this._billingAccount.set(response);
  //         },
  //         error: () => {}
  //       });
  //     } catch {
  //       console.warn('Failed to parse alp_auth_session');
  //     }
  //   }
  // }
}
