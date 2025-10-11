import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TicketCreation } from './ticketCreation';

describe('TicketCreation', () => {
  let component: TicketCreation;
  let fixture: ComponentFixture<TicketCreation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TicketCreation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TicketCreation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
