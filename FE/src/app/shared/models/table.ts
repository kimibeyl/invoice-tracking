import { Filter } from "../enums/filter";

export interface Column {
  field: string;
  header: string;
  type: Filter;
  sortField: string;
}
