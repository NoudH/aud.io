import { Component, OnInit } from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {SignupFormComponent} from '../signup-form/signup-form.component';

@Component({
  selector: 'app-login-form',
  templateUrl: './login-form.component.html',
  styleUrls: ['./login-form.component.css']
})
export class LoginFormComponent implements OnInit {

  constructor(private dialog: MatDialog) { }

  ngOnInit(): void {
  }

  openSignUpModal() {
    this.dialog.closeAll();
    this.dialog.open(SignupFormComponent);
  }
}
