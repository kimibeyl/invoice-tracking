import {Component, input} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {TopNavComponent} from './shared/components/top-nav/top-nav.component';
import {Toast} from 'primeng/toast';
import {MenuItem, MessageService} from 'primeng/api';
import {Drawer} from 'primeng/drawer';
import {Listbox} from 'primeng/listbox';
import {Button} from 'primeng/button';
import {Splitter} from 'primeng/splitter';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, TopNavComponent, Toast, Drawer, RouterLink, Listbox, Button, Splitter],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  providers: [MessageService]
})
export class AppComponent {
  title = 'invoice-tracking-invoice';

  items = input<MenuItem[]>([{
    label: 'Account Holders',
    icon: 'pi pi-fw pi-home',
    routerLink: ['/']
  },
    {
        label: 'Notifications',
        icon: 'pi pi-fw pi-bell',
        routerLink: ['/notifications']
    }
  ]);
}
