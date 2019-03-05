import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';
import { IFacturaVenta } from 'app/shared/model/factura-venta.model';
import { FacturaVentaService } from './factura-venta.service';
import { IClienteQueenBeer } from 'app/shared/model/cliente-queen-beer.model';
import { ClienteQueenBeerService } from 'app/entities/cliente-queen-beer';
import { IProductoQueenBeer } from 'app/shared/model/producto-queen-beer.model';
import { ProductoQueenBeerService } from 'app/entities/producto-queen-beer';
import { DetalleVenta, IDetalleVenta } from 'app/shared/model/detalle-venta.model';
import { EnvaseService } from 'app/entities/envase';
import { IEnvase } from 'app/shared/model/envase.model';

@Component({
    selector: 'jhi-factura-venta-update',
    templateUrl: './factura-venta-update.component.html'
})
export class FacturaVentaUpdateComponent implements OnInit {
    facturaVenta: IFacturaVenta;
    isSaving: boolean;
    productos: IProductoQueenBeer[];
    productosAlta: IProductoQueenBeer[];
    detalleVentas: IDetalleVenta[];
    detalleVenta: IDetalleVenta;
    clientes: IClienteQueenBeer[];
    envases: IEnvase[];
    fechaDp: any;
    productoId: number;
    cantidad: number;
    envaseId: number;

    constructor(
        protected jhiAlertService: JhiAlertService,
        protected facturaVentaService: FacturaVentaService,
        protected clienteService: ClienteQueenBeerService,
        protected activatedRoute: ActivatedRoute,
        protected productoService: ProductoQueenBeerService,
        protected envaseService: EnvaseService
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.productoId = null;
        this.envaseId = null;
        this.productosAlta = [];
        this.activatedRoute.data.subscribe(({ facturaVenta }) => {
            this.facturaVenta = facturaVenta;
        });
        this.facturaVenta.totalNeto = 0;
        this.clienteService
            .query({ filter: 'facturaventa-is-null' })
            .pipe(
                filter((mayBeOk: HttpResponse<IClienteQueenBeer[]>) => mayBeOk.ok),
                map((response: HttpResponse<IClienteQueenBeer[]>) => response.body)
            )
            .subscribe(
                (res: IClienteQueenBeer[]) => {
                    if (!this.facturaVenta.clienteId) {
                        this.clientes = res;
                    } else {
                        this.clienteService
                            .find(this.facturaVenta.clienteId)
                            .pipe(
                                filter((subResMayBeOk: HttpResponse<IClienteQueenBeer>) => subResMayBeOk.ok),
                                map((subResponse: HttpResponse<IClienteQueenBeer>) => subResponse.body)
                            )
                            .subscribe(
                                (subRes: IClienteQueenBeer) => (this.clientes = [subRes].concat(res)),
                                (subRes: HttpErrorResponse) => this.onError(subRes.message)
                            );
                    }
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
        this.productoService
            .query()
            .pipe(
                filter((mayBeOk: HttpResponse<IProductoQueenBeer[]>) => mayBeOk.ok),
                map((response: HttpResponse<IProductoQueenBeer[]>) => response.body)
            )
            .subscribe(
                (res: IProductoQueenBeer[]) => {
                    console.log(res);
                    this.productos = res;
                },
                (res: HttpErrorResponse) => this.onError(res.message)
            );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.facturaVenta.id !== undefined) {
            this.subscribeToSaveResponse(this.facturaVentaService.update(this.facturaVenta));
        } else {
            this.subscribeToSaveResponse(this.facturaVentaService.create(this.facturaVenta));
        }
    }

    protected subscribeToSaveResponse(result: Observable<HttpResponse<IFacturaVenta>>) {
        result.subscribe((res: HttpResponse<IFacturaVenta>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    protected onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    protected onSaveError() {
        this.isSaving = false;
    }

    protected onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackClienteById(index: number, item: IClienteQueenBeer) {
        return item.id;
    }

    addProductToList() {
        console.log(this.productoId);
        console.log(this.facturaVenta.totalNeto);
        this.productoService.find(this.productoId).subscribe(resp => {
            resp.body.cantidad = this.cantidad;
            resp.body.precioTotal = this.cantidad * resp.body.precioLitro;
            this.facturaVenta.totalNeto = this.facturaVenta.totalNeto + resp.body.precioTotal;
            console.log(resp);
            this.cantidad = null;
            this.productosAlta.push(resp.body);
            this.productoId = null;
        });
    }

    changeProducto(productoId: number) {
        console.log(productoId);
        this.envaseService.queryByProductoId(productoId).subscribe(resp => {
            console.log(resp);
            this.envases = resp.body;
        });
    }

    changeEnvase(envase: number) {
        console.log(envase);
        this.envaseService.find(envase).subscribe(resp => {
            console.log(resp);
        });
    }
}
