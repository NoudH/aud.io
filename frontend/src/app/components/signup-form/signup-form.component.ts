import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {LoginFormComponent} from '../login-form/login-form.component';

@Component({
  selector: 'app-signup-form',
  templateUrl: './signup-form.component.html',
  styleUrls: ['./signup-form.component.css']
})
export class SignupFormComponent implements OnInit {

  SignupCreds = {username: '', email: '', password: '', confirmPassword: ''};

  constructor(private dialog: MatDialog) { }

  ngOnInit(): void {  }

  openLoginModal() {
    this.dialog.closeAll();
    this.dialog.open(LoginFormComponent);
  }
}
