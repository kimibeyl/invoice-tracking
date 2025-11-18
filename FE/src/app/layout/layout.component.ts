import {Component, input} from '@angular/core';
import {Listbox} from "primeng/listbox";
import {RouterLink, RouterOutlet} from "@angular/router";
import {Splitter} from "primeng/splitter";
import {Toast} from "primeng/toast";
import {TopNavComponent} from "~/shared/components/top-nav/top-nav.component";
import {MenuItem, MessageService} from 'primeng/api';
import {Drawer} from 'primeng/drawer';
import {Button} from 'primeng/button';
import {Breadcrumb} from 'primeng/breadcrumb';
import {Menu} from 'primeng/menu';

@Component({
  selector: 'app-layout',
  imports: [RouterOutlet, TopNavComponent, Toast, RouterLink, Splitter, Button, Menu],
  templateUrl: './layout.component.html',
  styleUrl: './layout.component.scss',
  providers: [MessageService]
})
export class LayoutComponent {
  home = {icon: 'pi pi-home', routerLink: '/'};

  items = input<MenuItem[]>([{
    label: 'Navigate',
    items: [{
      label: 'Account Holders',
      icon: 'pi pi-fw pi-home',
      routerLink: '/'
    },
      {
        label: 'Notifications',
        icon: 'pi pi-fw pi-bell',
        routerLink: '/notifications'
      }]
  }]);
}
