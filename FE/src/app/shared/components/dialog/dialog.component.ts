import {
  Component,
  input,
  InputSignal,
  output,
  TemplateRef,
} from "@angular/core";
import { Dialog } from "primeng/dialog";
import { Button } from "primeng/button";
import { NgTemplateOutlet } from "@angular/common";
import { DialogProperties } from "~/shared/models/dialog";

@Component({
  selector: "app-dialog",
  imports: [Dialog, Button, NgTemplateOutlet],
  templateUrl: "./dialog.component.html",
  styleUrl: "./dialog.component.scss",
})
export class DialogComponent {
  dialog: InputSignal<DialogProperties> = input<DialogProperties>(
    {} as DialogProperties,
  );
  template: InputSignal<TemplateRef<any> | undefined> =
    input<TemplateRef<any | undefined>>();
  disabled = input(false);
  loading = input(false);

  // Output
  primaryActionChange = output<void>();
  secondaryActionChange = output<void>();
}
