import {SeObjectModel} from './se-object.model';

export class NetworkConnectionModel extends SeObjectModel {
  systemSlot0: string;
  systemSlot1: string;

  getQueryTag(): string {
    return '/network-connections';
  }
}
