import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { MoneyComponent } from './list/money.component';
import { MoneyDetailComponent } from './detail/money-detail.component';
import { MoneyUpdateComponent } from './update/money-update.component';
import { MoneyDeleteDialogComponent } from './delete/money-delete-dialog.component';
import { MoneyRoutingModule } from './route/money-routing.module';

@NgModule({
  imports: [SharedModule, MoneyRoutingModule],
  declarations: [MoneyComponent, MoneyDetailComponent, MoneyUpdateComponent, MoneyDeleteDialogComponent],
  entryComponents: [MoneyDeleteDialogComponent],
})
export class MoneyModule {}
