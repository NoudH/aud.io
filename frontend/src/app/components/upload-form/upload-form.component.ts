import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {TrackService} from '../../services/track-service/track-service.service';
import {Visibility} from '../../domain/visibility.enum';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-upload-form',
  templateUrl: './upload-form.component.html',
  styleUrls: ['./upload-form.component.css']
})
export class UploadFormComponent implements OnInit {
  filename: string;
  visibility = Visibility;
  showSpinner = false;
  submitted = false;
  file: File;

  public formGroup = this.formBuilder.group( {
    name: new FormControl('', [Validators.required]),
    visibilitySelect: new FormControl(0, [Validators.required]),
    fileUpload: new FormControl(null, [Validators.required])
  });

  // convenience getter for easy access to form fields
  get f() { return this.formGroup.controls; }

  constructor(private trackService: TrackService, private formBuilder: FormBuilder) { }

  ngOnInit(): void {
  }

  submit() {
    this.submitted = true;
    if (this.formGroup.valid) {
      console.log('check');
      this.trackService.postUploadTrack(
        this.f.name.value,
        Visibility[Visibility[this.f.visibilitySelect.value]],
        this.file
      )
        .subscribe((data) => {
          console.log('file uploaded');
          this.submitted = false;
        });
    }
  }

  handleFileInput(files: FileList) {
    this.filename = files[0].name;
    this.file = files[0];
  }
}
