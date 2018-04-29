import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SystemSlotComponent } from './system-slot.component';

describe('SystemSlotComponent', () => {
  let component: SystemSlotComponent;
  let fixture: ComponentFixture<SystemSlotComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SystemSlotComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SystemSlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
