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
import {SeObjectslistComponent} from './se-objectslist/se-objectslist.component';
import {SelectedSeObjectComponent} from './selected-se-object/selected-se-object.component';
import {SelectedFunctionComponent} from './selected-function/selected-function.component';
import {LiteralPropertyComponent} from './literal-property/literal-property.component';
import {IriPropertyComponent} from './iri-property/iri-property.component';
import {IriPropertyListComponent} from './iri-property-list/iri-property-list.component';
import {RealisationModuleComponent} from './realisation-module/realisation-module.component';
import {RequirementComponent} from './requirement/requirement.component';
import {SelectedRequirementComponent} from './selected-requirement/selected-requirement.component';
import { SelectedRealisationModuleComponent } from './selected-realisation-module/selected-realisation-module.component';
import { PerformanceComponent } from './performance/performance.component';
import { SelectedPerformanceComponent } from './selected-performance/selected-performance.component';
import { SystemInterfaceComponent } from './system-interface/system-interface.component';
import { SelectedSystemInterfaceComponent } from './selected-system-interface/selected-system-interface.component';

const appRoutes: Routes = [
  {path: 'datasets', component: DatasetComponent},
  {path: 'systemslots', component: SystemSlotComponent},
  {path: 'functions', component: FunctionComponent},
  {path: 'requirements', component: RequirementComponent},
  {path: 'realisationmodules', component: RealisationModuleComponent},
  {path: 'performances', component: PerformanceComponent},
  {path: 'systeminterfaces', component: SystemInterfaceComponent},
  {path: '', redirectTo: '/datasets', pathMatch: 'full'}
];

@NgModule({
  declarations: [
    AppComponent,
    SystemSlotComponent,
    DatasetComponent,
    SelectedSystemSlotComponent,
    FunctionComponent,
    SeObjectslistComponent,
    SelectedSeObjectComponent,
    SelectedFunctionComponent,
    LiteralPropertyComponent,
    IriPropertyComponent,
    IriPropertyListComponent,
    RealisationModuleComponent,
    RequirementComponent,
    SelectedRequirementComponent,
    SelectedRealisationModuleComponent,
    PerformanceComponent,
    SelectedPerformanceComponent,
    SystemInterfaceComponent,
    SelectedSystemInterfaceComponent
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
