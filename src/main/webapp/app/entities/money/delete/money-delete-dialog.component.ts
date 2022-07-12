import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMoney } from '../money.model';
import { MoneyService } from '../service/money.service';

@Component({
  templateUrl: './money-delete-dialog.component.html',
})
export class MoneyDeleteDialogComponent {
  money?: IMoney;

  constructor(protected moneyService: MoneyService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.moneyService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
