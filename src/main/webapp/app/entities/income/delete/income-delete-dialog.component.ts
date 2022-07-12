import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IIncome } from '../income.model';
import { IncomeService } from '../service/income.service';

@Component({
  templateUrl: './income-delete-dialog.component.html',
})
export class IncomeDeleteDialogComponent {
  income?: IIncome;

  constructor(protected incomeService: IncomeService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.incomeService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
