package bazel

import devteam.rx.Observable

interface Converter<TSource, TDestination> {
    fun convert(source: TSource): TDestination
}