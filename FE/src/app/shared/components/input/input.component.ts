import { Component, input, signal } from "@angular/core";
import { FormControl, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { InputText } from "primeng/inputtext";

@Component({
  selector: "app-input",
  imports: [FormsModule, InputText, ReactiveFormsModule],
  templateUrl: "./input.component.html",
  styleUrl: "./input.component.scss",
})
export class InputComponent {
  control = input<FormControl>(new FormControl(""));
  placeholder = input<string>("");
  errorMessage = input<string | null>(null);
}
