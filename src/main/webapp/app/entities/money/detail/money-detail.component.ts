import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMoney } from '../money.model';

@Component({
  selector: 'jhi-money-detail',
  templateUrl: './money-detail.component.html',
})
export class MoneyDetailComponent implements OnInit {
  money: IMoney | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ money }) => {
      this.money = money;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
