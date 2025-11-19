import { AbstractControl, ValidationErrors, ValidatorFn } from "@angular/forms";

export function validateEmail(): ValidatorFn {
  const email: RegExp =
    /^(?=.{1,254}$)(?=.{1,64}@)[\p{L}\p{N}._%+\-]+@(?:[\p{L}\p{N}\-]+\.)+[\p{L}]{2,}$/u;
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value || control.value.length === 0) {
      return null;
    }
    const forbidden = !email.test(control.value);
    return forbidden ? { forbiddenEmail: { value: control.value } } : null;
  };
}
