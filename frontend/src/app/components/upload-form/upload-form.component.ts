import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {TrackService} from '../../services/track-service/track.service';
import {Visibility} from '../../domain/visibility.enum';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {FormValidationErrorMatcher} from '../../objects/error-state-matchers/form-validation-error-matcher';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatDialog} from '@angular/material/dialog';

@Component({
  selector: 'app-upload-form',
  templateUrl: './upload-form.component.html',
  styleUrls: ['./upload-form.component.css']
})
export class UploadFormComponent implements OnInit {
  filename: string;
  visibility = Visibility;
  showSpinner = false;
  file: File;
  validationErrorMatcher = new FormValidationErrorMatcher();

  public formGroup = this.formBuilder.group( {
    name: new FormControl('', [Validators.required]),
    visibilitySelect: new FormControl(0, [Validators.required]),
    fileUpload: new FormControl(null, [Validators.required])
  });

  // convenience getter for easy access to form fields
  get f() { return this.formGroup.controls; }

  constructor(private snackbar: MatSnackBar, private dialog: MatDialog, private trackService: TrackService, private formBuilder: FormBuilder) { }

  ngOnInit(): void {
  }

  submit() {
    this.showSpinner = true;
    if (this.formGroup.valid) {
      this.trackService.postUploadTrack(
        this.f.name.value,
        Visibility[Visibility[this.f.visibilitySelect.value]],
        this.file
      )
        .subscribe(
          (data) => {
            this.showSpinner = false;
            this.dialog.closeAll();
            this.snackbar.open('Upload was successful!', undefined, {
              duration: 200,
              verticalPosition: 'top'
            });
          }, error1 => {
            this.showSpinner = false;
            this.snackbar.open(error1.error.error ? error1.error.error : error1.error, undefined, {
              duration: 5000,
              verticalPosition: 'top',
              panelClass: 'error-panel'
            });
          }
        );
    }
  }

  handleFileInput(files: FileList) {
    this.filename = files[0].name;
    this.file = files[0];
  }
}
