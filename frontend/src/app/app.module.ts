import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatSliderModule } from '@angular/material/slider';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { AudioplayerComponent } from './components/audioplayer/audioplayer.component';
import { MatMenuModule } from '@angular/material/menu';
import { AudiolistComponent } from './components/audiolist/audiolist.component';
import {MatDividerModule} from '@angular/material/divider';
import {MatListModule} from '@angular/material/list';
import {MatRippleModule} from '@angular/material/core';
import {MatTableModule} from '@angular/material/table';
import {MinuteSecondsPipe} from './objects/pipes/minute-seconds/minute-seconds-pipe';
import {MatSidenavModule} from '@angular/material/sidenav';
import { LoginFormComponent } from './components/login-form/login-form.component';
import { SignupFormComponent } from './components/signup-form/signup-form.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {HttpClientModule, HTTP_INTERCEPTORS} from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import {MatDialogModule} from '@angular/material/dialog';
import {AuthInterceptor} from './objects/interceptors/auth-interceptor/auth-interceptor';
import { UploadFormComponent } from './components/upload-form/upload-form.component';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import { EnumToArrayPipe } from './objects/pipes/enum-to-array/enum-to-array.pipe';
import {MatSelectModule} from '@angular/material/select';

const appRoutes: Routes = [
  {}
];

@NgModule({
  declarations: [
    AppComponent,
    AudioplayerComponent,
    AudiolistComponent,
    MinuteSecondsPipe,
    LoginFormComponent,
    SignupFormComponent,
    UploadFormComponent,
    EnumToArrayPipe
  ],
  imports: [
    /*RouterModule.forRoot(appRoutes),*/
    BrowserModule,
    BrowserAnimationsModule,
    MatSliderModule,
    MatButtonModule,
    MatToolbarModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
    MatMenuModule,
    MatDividerModule,
    MatListModule,
    MatRippleModule,
    MatTableModule,
    MatSidenavModule,
    FormsModule,
    ReactiveFormsModule,
    MatCardModule,
    HttpClientModule,
    RouterModule,
    MatDialogModule,
    MatProgressSpinnerModule,
    MatSelectModule
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
