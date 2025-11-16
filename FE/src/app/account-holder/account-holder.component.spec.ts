import { ComponentFixture, TestBed } from '@angular/core/testing';
import {AccountHolderComponent} from '~/account-holder/account-holder.component';

describe('AccountHolderComponent', () => {
  let component: AccountHolderComponent;
  let fixture: ComponentFixture<AccountHolderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AccountHolderComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AccountHolderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
