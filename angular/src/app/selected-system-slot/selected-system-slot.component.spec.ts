import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectedSystemSlotComponent } from './selected-system-slot.component';

describe('SelectedSystemSlotComponent', () => {
  let component: SelectedSystemSlotComponent;
  let fixture: ComponentFixture<SelectedSystemSlotComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SelectedSystemSlotComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectedSystemSlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
