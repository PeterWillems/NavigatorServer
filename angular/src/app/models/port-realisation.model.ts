import {SeObjectModel} from './se-object.model';

export class PortRealisationModel extends SeObjectModel {

  getQueryTag(): string {
    return '/port-realisations';
  }
}
