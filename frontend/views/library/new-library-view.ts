import '@vaadin/vaadin-button';
import '@vaadin/vaadin-date-picker';
import '@vaadin/vaadin-date-time-picker';
import '@vaadin/vaadin-form-layout';
import '@vaadin/vaadin-grid';
import '@vaadin/vaadin-grid/vaadin-grid-column';
import '@vaadin/vaadin-ordered-layout/vaadin-horizontal-layout';
import '@vaadin/vaadin-split-layout';
import '@vaadin/vaadin-text-field';
import '@vaadin/vaadin-combo-box';

// import '@vaadin/vaadin-search-field';
import { html, LitElement } from 'lit';
import { customElement } from 'lit/decorators.js';

@customElement('new-library-view')
export class NewLibraryView extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`
      <div class="flex-grow w-full" id="grid-wrapper">
        <vaadin-grid id="grid"></vaadin-grid>
      </div>
      <div>
        <vaadin-text-field label="Search" id="searchField"></vaadin-text-field>
        <vaadin-combo-box id="borrowed-combobox"></vaadin-combo-box>
      </div>
      `;
  }
}
