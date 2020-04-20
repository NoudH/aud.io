import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {SignupFormComponent} from '../signup-form/signup-form.component';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth-service/auth.service';
import {FormValidationErrorMatcher} from '../../objects/error-state-matchers/form-validation-error-matcher';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit {

  validationErrorMatcher = new FormValidationErrorMatcher();

  public formGroup = this.formBuilder.group( {
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required])
  });

  constructor(
    private dialog: MatDialog,
    private snackbar: MatSnackBar,
    private formBuilder: FormBuilder,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
  }

  // convenience getter for easy access to form fields
  get f() { return this.formGroup.controls; }

  openSignUpModal() {
    this.dialog.closeAll();
    this.dialog.open(SignupFormComponent);
  }

  submit() {
    if (this.formGroup.valid) {
      this.authService.postLogin(this.f.username.value, this.f.password.value)
        .subscribe(
          (data: string) => {
            this.dialog.closeAll();
            localStorage.setItem('token', data);
            this.snackbar.open('Welcome back!', undefined, {
              duration: 2000,
              verticalPosition: 'top'
            });
          },
          (error1) => {
            this.snackbar.open(error1.error.error ? error1.error.error : error1.error, undefined, {
              duration: 5000,
              verticalPosition: 'top',
              panelClass: 'error-panel'
            });
          }
        );
    }
  }
}
