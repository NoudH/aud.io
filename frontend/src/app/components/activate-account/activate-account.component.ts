import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute, ParamMap } from '@angular/router';
import {AuthService} from '../../services/auth-service/auth.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrls: ['./activate-account.component.css']
})
export class ActivateAccountComponent implements OnInit {

  showSpinner = true;
  success: boolean;
  errortext: string;

  constructor(private snackbar: MatSnackBar, private activatedRoute: ActivatedRoute, private authService: AuthService) { }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.authService.putActivate(params.token)
        .subscribe(
          (data) => {
            this.showSpinner = false;
            this.success = true;
          },
          (error1) => {
            this.showSpinner = false;
            this.success = false;
            this.errortext = error1.error.error ? error1.error.error : error1.error;
          }
        );
    });
  }

}
