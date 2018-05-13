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
import {SeObjectType} from './se-object-type';
import {HamburgerModel} from './models/hamburger.model';
import {PerformanceModel} from './models/performance.model';
import {RealisationPortModel} from './models/realisation-port.model';

@Injectable()
export class RealisationModuleService extends SeObjectService {
  apiAddress: string;
  dataset: Dataset;
  realisationModules: RealisationModuleModel[];
  selectedRealisationModule: RealisationModuleModel;

  constructor(protected _httpClient: HttpClient, private _datasetService: DatasetService) {
    super(_httpClient);
    this.apiAddress = 'http://localhost:8080/se';
    this._datasetService.selectedDatasetUpdated.subscribe(value => {
      this.dataset = value;
      console.log('RealisationModuleService: new dataset: ' + this.dataset.filepath);
      this.selectedRealisationModule = null;
      this.loadObjects(this.dataset);
    });
  }

  loadObjects(dataset: Dataset): void {
    this.load(this.selectedRealisationModule, this.dataset, SeObjectType.RealisationModuleModel).subscribe(value => {
      this.realisationModules = value;
      this.seObjectsUpdated.emit(this.realisationModules);
    });
  }

  createObject(): void {
    this.create(this.selectedRealisationModule, this.dataset, SeObjectType.RealisationModuleModel).subscribe(value => {
      this.realisationModules.push(value);
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

  selectRealisationModule(selectedRealisationModule: RealisationModuleModel) {
    this.selectedRealisationModule = selectedRealisationModule;
  }


  getSeObjectLabel(systemSlotUri: string): string {
    return this.getSeObject(systemSlotUri).label;
  }

  getSeObjects(): RealisationModuleModel[] {
    return this.realisationModules;
  }

  getRealisationPorts(realisationModuleUri: string): Observable<Array<RealisationPortModel>> {
    const hashMark = realisationModuleUri.indexOf('#') + 1;
    const localName = realisationModuleUri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/realisation-modules/' + localName + '/ports';
    return this._httpClient.get<Array<RealisationPortModel>>(request);
  }

  getHamburgers(realisationModuleUri: string): Observable<Array<HamburgerModel>> {
    const hashMark = realisationModuleUri.indexOf('#') + 1;
    const localName = realisationModuleUri.substring(hashMark);
    const request = this.apiAddress + '/datasets/' + this.dataset.id + '/realisation-modules/' + localName + '/hamburgers';
    return this._httpClient.get<Array<HamburgerModel>>(request);
  }
}
