import {Component, inject} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {invoiceAuthenticationApiService} from '~/api/services/invoice-authentication-api.service';
import {Login$Params} from '~/api/fn/authentication-api/login';
import {Card} from 'primeng/card';
import {Password} from 'primeng/password';
import {Button} from 'primeng/button';
import {InputComponent} from '~/shared/components/input/input.component';
import {MessageService} from 'primeng/api';
import {Toast} from 'primeng/toast';
import {Router} from '@angular/router';
import {validateEmail} from '~/shared/utils/validators';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    Card,
    Password,
    Button,
    InputComponent,
    Toast
  ],
  templateUrl: './login.component.html',
  providers: [MessageService]
})
export class LoginComponent {
  router = inject(Router)
  fb = inject(FormBuilder);
  invoiceAuthenticationService = inject(invoiceAuthenticationApiService);
  messageService = inject(MessageService);

  loginForm: FormGroup = this.fb.group({
    username: this.fb.control('', [Validators.required, validateEmail()]),
    password: ['', Validators.required]
  });

  onSubmit() {
    const params: Login$Params = {
      body: {
        username: this.loginForm.value.username,
        password: this.loginForm.value.password
      }
    }
    this.invoiceAuthenticationService.login(params).subscribe({
      next: (result) => {
          localStorage.setItem('token', result['token']);
          this.router.navigate(['/'])
      }, error: error => {
        console.log(error)
        this.messageService.add({severity: 'error', summary: 'Error', detail: 'Invalid credentials'})
      }}
    )
  }
}
