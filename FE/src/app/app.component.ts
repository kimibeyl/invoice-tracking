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
  imports: [
    RouterOutlet,
    Toast
  ],
  templateUrl: './app.component.html'
})
export class AppComponent {
  title = 'invoice-tracking-invoice';
}
