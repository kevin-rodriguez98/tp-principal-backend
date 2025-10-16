USE fl_insumos;

DROP VIEW IF EXISTS view_insumos;
CREATE VIEW view_insumos AS
    SELECT
        i.id as 'id',
        i.codigo as 'codigo',
        i.nombre as 'nombre',
        c.nombre as 'categoria',
        u.nombre as 'unidad',
        i.stock_actual as 'stock actual',
        i.umbral_minimo_stock as 'umbral minimo stock',
        IF(i.stock_actual < i.umbral_minimo_stock, 'true', 'false') as 'stock crítico',
        i.alta as 'fecha alta',
        usuario.nombre as 'usario dador alta'
    FROM
        insumo i
    JOIN categoria c ON
        c.id = i.id_categoria
    JOIN unidad u ON
        u.id = i.id_unidad
    LEFT JOIN fl_usuarios.usuario AS usuario ON
        usuario.id = i.id_usuario_dador_alta
    ORDER BY i.codigo;

DROP VIEW IF EXISTS view_prod_insumos;
CREATE VIEW view_prod_insumos AS
    SELECT
        pi.id as 'id',
        pi.codigo as 'codigo',
        i.nombre as 'insumo',
        c.nombre as 'categoria',
        f.nombre as 'marca',
        u.nombre as 'unidad',
        pi.cantidad_aportada as 'cantidad aportada',
        pi.stock_actual as 'stock actual',
        pi.alta as 'fecha alta',
        usuario.nombre as 'usuario dador alta'
    FROM
        prod_insumo pi
    JOIN insumo i ON
        i.id = pi.id_insumo
    JOIN categoria c ON
        c.id = i.id_categoria
    JOIN fabricante f ON
        f.id = pi.id_fabricante
    JOIN unidad u ON
        u.id = i.id_unidad
    LEFT JOIN fl_usuarios.usuario as usuario ON
        usuario.id = pi.id_usuario_dador_alta
    ORDER BY i.id;