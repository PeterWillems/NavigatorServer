import {SeObjectModel} from './se-object.model';

export class PortRealisationModel extends SeObjectModel {
  systemInterface: string;
  realisationPort: string;

  getQueryTag(): string {
    return '/port-realisations';
  }
}
