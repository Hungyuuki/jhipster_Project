import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'income',
        data: { pageTitle: 'jHipsterProjectApp.income.home.title' },
        loadChildren: () => import('./income/income.module').then(m => m.IncomeModule),
      },
      {
        path: 'money',
        data: { pageTitle: 'jHipsterProjectApp.money.home.title' },
        loadChildren: () => import('./money/money.module').then(m => m.MoneyModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
