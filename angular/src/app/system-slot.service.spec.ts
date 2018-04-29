import { TestBed, inject } from '@angular/core/testing';

import { SystemSlotService } from './system-slot.service';

describe('SystemSlotService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SystemSlotService]
    });
  });

  it('should be created', inject([SystemSlotService], (service: SystemSlotService) => {
    expect(service).toBeTruthy();
  }));
});
