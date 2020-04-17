import {ErrorStateMatcher} from '@angular/material/core';

export class FormValidationErrorMatcher implements ErrorStateMatcher {
  isErrorState(control, form) {
    return control && control.invalid && form && form.submitted;
  }
}
