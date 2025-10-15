import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SidebarComponent } from './sidebar.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('SidebarComponent', () => {
  let component: SidebarComponent;
  let fixture: ComponentFixture<SidebarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SidebarComponent],
      imports: [RouterTestingModule] // pour que routerLinkActive et routerLink ne posent pas d'erreurs
    })
    .compileComponents();

    fixture = TestBed.createComponent(SidebarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the sidebar component', () => {
    expect(component).toBeTruthy();
  });

  it('should have menu items defined', () => {
    expect(component.menuItems.length).toBeGreaterThan(0);
  });

  it('should toggle collapse state', () => {
    const initial = component.isCollapsed;
    component.isCollapsed = !initial;
    expect(component.isCollapsed).toBe(!initial);
  });
});
