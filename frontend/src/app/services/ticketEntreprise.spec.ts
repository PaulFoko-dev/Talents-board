import { TestBed } from '@angular/core/testing';

import { TicketEntrepriseService } from './ticketEntreprise.service';

describe('Ticket', () => {
  let service: TicketEntrepriseService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TicketEntrepriseService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
