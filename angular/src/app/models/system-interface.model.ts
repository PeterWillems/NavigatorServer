import {SeObjectModel} from './se-object.model';

export class SystemInterfaceModel extends SeObjectModel {
  systemSlot0: string;
  systemSlot1: string;
  requirements: string[];

  getQueryTag(): string {
    return '/system-interfaces';
  }
}
