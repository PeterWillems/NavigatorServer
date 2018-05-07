import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {SystemSlotModel} from './models/system-slot.model';
import {Dataset} from './dataset/dataset.model';
import {SeObjectService} from './se-object.service';
import {SeObjectModel} from './models/se-object.model';
import {Observable} from 'rxjs/Observable';
import {FunctionModel} from './models/function.model';
import {DatasetService} from './dataset.service';
import {RealisationModuleModel} from './models/realisation-module.model';

@Injectable()
export class RealisationModuleService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  realisationModules: RealisationModuleModel[];
  selectedRealisationModule: RealisationModuleModel;

  constructor(private _httpClient: HttpClient, private _datasetService: DatasetService) {
    super();
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('RealisationModuleService: new dataset: ' + this.dataset.filepath);
      this.loadRealisationModules(this.dataset);
    });
  }

  loadRealisationModules(dataset: Dataset): void {
    this.selectedRealisationModule = null;
    this.dataset = dataset;
    console.log('loadRealisationModules: ' + this.dataset.id);
    let realisationModules: Array<RealisationModuleModel> = [];
    const request = this.apiAddress + '/datasets/' + dataset.id + '/realisation-modules';
    const realisationModules$ = this._httpClient.get<Array<RealisationModuleModel>>(request);
    realisationModules$.subscribe(value => {
      realisationModules = value;
    }, error => {
      console.log(error);
    }, () => {
      this.realisationModules = realisationModules;
      this.seObjectsUpdated.emit(realisationModules);
    });
  }

  createSeObject(): void {
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/realisation-modules';
    let realisationModule = <RealisationModuleModel>{uri: 'xxx', label: '', assembly: ''};
    const realisationModule$ = this._httpClient.post<RealisationModuleModel>(request, realisationModule);
    realisationModule$.subscribe(value => {
      realisationModule = value;
    }, error => {
      console.log(error);
    }, () => {
      this.realisationModules.push(realisationModule);
      this.seObjectsUpdated.emit(this.realisationModules);
    });
  }

  updateSeObject(realisationModule: RealisationModuleModel): void {
    const hashMark = realisationModule.uri.indexOf('#') + 1;
    const localName = realisationModule.uri.substring(hashMark);
    console.log('update: ' + this.dataset.id + ' system slot: ' + localName + ' assembly: ' + realisationModule.assembly);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/realisation-modules/' + localName;
    this._httpClient.put(request, realisationModule).subscribe(value => {
    }, error => {
    }, () => {
    });
  }

  getSeObject(uri: string): RealisationModuleModel {
    for (let index = 0; index < this.realisationModules.length; index++) {
      if (this.realisationModules[index].uri === uri) {
        return this.realisationModules[index];
      }
    }
    return null;
  }

  selectSystemSlot(selectedRealisationModule: RealisationModuleModel) {
    this.selectedRealisationModule = selectedRealisationModule;
  }


  getSeObjectLabel(systemSlotUri: string): string {
    return this.getSeObject(systemSlotUri).label;
  }

  getSeObjects(): RealisationModuleModel[] {
    return this.realisationModules;
  }
}
