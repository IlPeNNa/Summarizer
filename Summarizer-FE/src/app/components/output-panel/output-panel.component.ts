import { Component, Input, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-output-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './output-panel.component.html',
  styleUrl: './output-panel.component.css'
})
export class OutputPanelComponent {
  @Input() summary: string = '';
  @Input() originalLength: number = 0;
  @Input() summaryLength: number = 0;
  @Input() isLoading: boolean = false;
  
  @Output() downloadTxtRequested = new EventEmitter<void>();
  @Output() downloadDocxRequested = new EventEmitter<void>();
  @Output() downloadPdfRequested = new EventEmitter<void>();

  get reductionPercentage(): number {
    if (this.originalLength === 0) return 0;
    return ((1 - this.summaryLength / this.originalLength) * 100);
  }

  get hasSummary(): boolean {
    return this.summary.length > 0;
  }

  onDownloadTxt(): void {
    this.downloadTxtRequested.emit();
  }

  onDownloadDocx(): void {
    this.downloadDocxRequested.emit();
  }

  onDownloadPdf(): void {
    this.downloadPdfRequested.emit();
  }
}
