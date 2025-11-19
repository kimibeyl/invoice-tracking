import { Component, DestroyRef, inject, OnInit, signal } from "@angular/core";
import { MenubarModule } from "primeng/menubar";
import { BadgeModule } from "primeng/badge";
import { OverlayBadgeModule } from "primeng/overlaybadge";
import { Popover } from "primeng/popover";
import { Pageable } from "~/api/models/pageable";
import { PageNotificationResponse } from "~/api/models/page-notification-response";
import { invoiceNotificationApiService } from "~/api/services/invoice-notification-api.service";
import { NotificationSearchRequest } from "~/api/models/notification-search-request";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Button } from "primeng/button";
import { Router } from "@angular/router";
import { NgOptimizedImage } from "@angular/common";

@Component({
  selector: "app-top-nav",
  imports: [
    MenubarModule,
    BadgeModule,
    OverlayBadgeModule,
    Popover,
    Button,
    NgOptimizedImage,
  ],
  templateUrl: "./top-nav.component.html",
})
export class TopNavComponent implements OnInit {
  notifications = signal<PageNotificationResponse>({});
  notificationService = inject(invoiceNotificationApiService);
  destroyRef = inject(DestroyRef);
  router = inject(Router);

  pageable = signal<Pageable>({
    page: 0,
    size: 5,
    sort: [],
  });

  ngOnInit(): void {
    const params = {
      pageable: this.pageable(),
      body: {
        status: "UNREAD",
        type: "IMPORTANT",
      } as NotificationSearchRequest,
    };

    this.notificationService
      .getAllNotifications(params)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (result) => {
          if (result) {
            this.notifications.set(result);
          }
        },
      });
  }

  navigateToNotifications() {
    this.router.navigate(["notifications"]);
  }

  logout() {
    localStorage.removeItem("token");
    this.router.navigate(["auth"]);
  }
}
