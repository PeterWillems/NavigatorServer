import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule, Routes} from '@angular/router';
import {HttpClientModule} from '@angular/common/http';

import {AppComponent} from './app.component';
import {SystemSlotComponent} from './system-slot/system-slot.component';
import {DatasetComponent} from './dataset/dataset.component';
import {SelectedSystemSlotComponent} from './selected-system-slot/selected-system-slot.component';
import {FunctionComponent} from './function/function.component';

const appRoutes: Routes = [
  {path: 'datasets', component: DatasetComponent},
  {path: 'systemslots', component: SystemSlotComponent},
  {path: 'functions', component: FunctionComponent},
  {path: '', redirectTo: '/datasets', pathMatch: 'full'}
];

@NgModule({
  declarations: [
    AppComponent,
    SystemSlotComponent,
    DatasetComponent,
    SelectedSystemSlotComponent,
    FunctionComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    RouterModule.forRoot(appRoutes),
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
