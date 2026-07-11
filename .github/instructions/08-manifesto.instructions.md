---
applyTo: "**/AndroidManifest.xml"
---
# Manifesto e Componentes

## Regras

- TODO componente (`Activity`, `Service`, `Receiver`, `Provider`), permissão e
  metadado deve ser declarado em `AndroidManifest.xml`. Omissão = falha em execução.
- Toda nova `Activity` precisa de uma entrada `<activity>`.
- A activity de entrada mantém o `intent-filter` com `MAIN` + `LAUNCHER`.
- Novas activities normalmente usam `android:exported="false"` (a menos que precisem
  ser abertas por outros apps/sistema).

## Faça

```xml
<application ...>
    <activity
        android:name=".MainActivity"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
        </intent-filter>
    </activity>

    <!-- Nova tela como Activity precisa ser declarada -->
    <activity
        android:name=".DetalheActivity"
        android:exported="false" />
</application>
```

Permissões declaradas antes do `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

