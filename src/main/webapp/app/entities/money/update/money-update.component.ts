import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IMoney, Money } from '../money.model';
import { MoneyService } from '../service/money.service';

@Component({
  selector: 'jhi-money-update',
  templateUrl: './money-update.component.html',
})
export class MoneyUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [],
    roll: [null, [Validators.required]],
    income: [null, [Validators.required]],
  });

  constructor(protected moneyService: MoneyService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ money }) => {
      this.updateForm(money);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const money = this.createFromForm();
    if (money.id !== undefined) {
      this.subscribeToSaveResponse(this.moneyService.update(money));
    } else {
      this.subscribeToSaveResponse(this.moneyService.create(money));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMoney>>): void {
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

  protected updateForm(money: IMoney): void {
    this.editForm.patchValue({
      id: money.id,
      name: money.name,
      roll: money.roll,
      income: money.income,
    });
  }

  protected createFromForm(): IMoney {
    return {
      ...new Money(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      roll: this.editForm.get(['roll'])!.value,
      income: this.editForm.get(['income'])!.value,
    };
  }
}
