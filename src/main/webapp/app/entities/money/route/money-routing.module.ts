import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { MoneyComponent } from '../list/money.component';
import { MoneyDetailComponent } from '../detail/money-detail.component';
import { MoneyUpdateComponent } from '../update/money-update.component';
import { MoneyRoutingResolveService } from './money-routing-resolve.service';

const moneyRoute: Routes = [
  {
    path: '',
    component: MoneyComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: MoneyDetailComponent,
    resolve: {
      money: MoneyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: MoneyUpdateComponent,
    resolve: {
      money: MoneyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: MoneyUpdateComponent,
    resolve: {
      money: MoneyRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(moneyRoute)],
  exports: [RouterModule],
})
export class MoneyRoutingModule {}
