import {Component, input, InputSignal, model, output, signal, TemplateRef} from '@angular/core';
import {TableLazyLoadEvent, TableModule, TableRowSelectEvent} from 'primeng/table';
import {Column} from '~/shared/models/table';
import {Tag} from 'primeng/tag';
import {CommonModule} from '@angular/common';
import {Filter} from '~/shared/enums/filter';
import {Skeleton} from 'primeng/skeleton';
import {FormGroup, ReactiveFormsModule} from '@angular/forms';
import {InputText} from 'primeng/inputtext';
import {Select} from 'primeng/select';
import { MultiSelectModule } from 'primeng/multiselect';
import {DatePicker} from 'primeng/datepicker';

@Component({
  selector: 'app-table',
  imports: [
    TableModule,
    Tag,
    CommonModule,
    Skeleton,
    ReactiveFormsModule,
    InputText,
    Select,
    MultiSelectModule,
    DatePicker
  ],
  templateUrl: './table.component.html'
})
export class TableComponent {
  items: InputSignal<any[]> = input<any[]>([]);
  cols: InputSignal<Column[]> = input<Column[]>([{} as Column]);
  loading: InputSignal<boolean> = input<boolean>(false);
  showCheckbox = input(false);
  selected = model<any | null>(null);
  sortField: InputSignal<string> = input<string>('createdAt');
  sortOrder: InputSignal<number> = input<number>(-1);
  totalRecords = input<number>(0);
  inputForm = input<FormGroup>(new FormGroup({}));

  selectedItemsTemplate: InputSignal<TemplateRef<any> | undefined> = input<TemplateRef<any | undefined>>();

  onLazyLoad = output<TableLazyLoadEvent>();
  onSelectionChange = output<TableRowSelectEvent>()
  onRowSelect = output<any>()


  protected readonly Filter = Filter;
}
