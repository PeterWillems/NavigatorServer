import {Component} from '@angular/core';
import {SystemSlotService} from './system-slot.service';
import {DatasetService} from './dataset.service';
import {FunctionService} from './function.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
  providers: [DatasetService, SystemSlotService, FunctionService]
})
export class AppComponent {
  title = 'COINS SE Navigator';
}
