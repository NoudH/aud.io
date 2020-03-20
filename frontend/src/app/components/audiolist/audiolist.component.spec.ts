import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AudiolistComponent } from './audiolist.component';

describe('AudiolistComponent', () => {
  let component: AudiolistComponent;
  let fixture: ComponentFixture<AudiolistComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AudiolistComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AudiolistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
