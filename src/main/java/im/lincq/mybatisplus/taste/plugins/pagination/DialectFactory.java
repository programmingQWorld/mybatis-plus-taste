�}N�
 3  �� p�@��qw
�� �:�Y.�ֵ	F�u�M\f�����>�%7��м e����Ҁl	Z������ ��u�H#��p���
�?���+�׼�����݋�7���!����h�ES����nrR~��$�wZ]�ѥp�cٗ�1����ŜyYo޺��{�P�:e�e����1���u��M�9ڷ�_��،
��I;�NB�R%�����S�'*�Ԇef1��gj�LW}�R���q`��Ԓ)���3�q�	쵥K�z�5F�\rM�t*K�p0������F �3�`4�'�๒��fV٭��
�ؕ�a+����;�A�."h�p	�����A�SR$3�n�!ʋi������=sh��y�h���Ԑ�!�ZR�>G�X���O͏!ኌ��E{��P(�ʰ?@�,C� G Q_(�i��f�ay����&&[����vKCƱ-pk:� �r+�!��::�)9�a�7@������9�>e(dbtype)) {
            return new HSQLDialect();
        } else if ("sqlite".equalsIgnoreCase(dbtype)) {
            return new SQLiteDialect();
        } else if ("postgre".equalsIgnoreCase(dbtype)) {
            return new PostgreDialect();
        } else {
            return null;
        }
    }
}
