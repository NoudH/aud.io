import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {LoginFormComponent} from '../login-form/login-form.component';
import {FormValidationErrorMatcher} from '../../objects/error-state-matchers/form-validation-error-matcher';
import {FormBuilder, FormControl, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth-service/auth.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-signup-form',
  templateUrl: './signup-form.component.html',
  styleUrls: ['./signup-form.component.css']
})
export class SignupFormComponent implements OnInit {

  validationErrorMatcher = new FormValidationErrorMatcher();

  public formGroup = this.formBuilder.group( {
    username: new FormControl('', [Validators.required]),
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    confirmPassword: new FormControl('', [Validators.required])
  });

  constructor(private dialog: MatDialog, private formBuilder: FormBuilder, private authService: AuthService, private snackbar: MatSnackBar) { }

  ngOnInit(): void {  }

  // convenience getter for easy access to form fields
  get f() { return this.formGroup.controls; }

  openLoginModal() {
    this.dialog.closeAll();
    this.dialog.open(LoginFormComponent);
  }

  submit() {
    if (this.formGroup.valid) {
      this.authService.postSignUp(
        this.f.username.value,
        this.f.email.value,
        this.f.password.value
      )
        .subscribe(
          (data) => {
            this.dialog.closeAll();
            this.snackbar.open('Sign up was successful! Please check your email to confirm your registration.', undefined, {
              duration: 5000,
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
