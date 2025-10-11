import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketCandidat } from './ticketCandidat';

describe('TicketCandidat', () => {
  let component: TicketCandidat;
  let fixture: ComponentFixture<TicketCandidat>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketCandidat]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketCandidat);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
