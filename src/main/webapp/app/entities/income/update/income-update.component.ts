import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IIncome, Income } from '../income.model';
import { IncomeService } from '../service/income.service';

@Component({
  selector: 'jhi-income-update',
  templateUrl: './income-update.component.html',
})
export class IncomeUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    roll: [],
    income: [null, [Validators.required]],
  });

  constructor(protected incomeService: IncomeService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ income }) => {
      this.updateForm(income);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const income = this.createFromForm();
    if (income.id !== undefined) {
      this.subscribeToSaveResponse(this.incomeService.update(income));
    } else {
      this.subscribeToSaveResponse(this.incomeService.create(income));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIncome>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(income: IIncome): void {
    this.editForm.patchValue({
      id: income.id,
      name: income.name,
      roll: income.roll,
      income: income.income,
    });
  }

  protected createFromForm(): IIncome {
    return {
      ...new Income(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      roll: this.editForm.get(['roll'])!.value,
      income: this.editForm.get(['income'])!.value,
    };
  }
}
