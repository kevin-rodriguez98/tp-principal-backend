USE fl_registros_stock;

DROP VIEW IF EXISTS view_ingresos_insumos;
CREATE VIEW view_ingresos_insumos AS
    SELECT
        ii.id as 'ingreso_insumo id',
        ii.fecha as 'fecha ingreso',
        i.nombre as 'insumo',
        f.nombre as 'marca',
        ii.lote as 'lote',
        ii.cantidad_inicial as 'cantidad inicial',
        ii.cantidad_restante as 'cantidad restante'
    FROM
        ingreso_insumo ii
    JOIN fl_insumos.prod_insumo AS pi ON
        pi.id = ii.id_prod_insumo
    JOIN fl_insumos.insumo AS i ON
        pi.id_insumo = i.id
    JOIN fl_insumos.fabricante AS f ON
        pi.id_fabricante = f.id
;